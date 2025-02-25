package com.ueh.fieldbooking.services;

import com.ueh.fieldbooking.dtos.BookingDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class VNPayService {
    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String key;

    @Value("${vnpay.payment-url}")
    private String paymentUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    public String createUrl(HttpServletRequest request, BookingDTO dto) {
        Map<String,String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Command", "pay");
        int amount = 50000;
        params.put("vnp_Amount", String.valueOf(amount *100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(dto.getId()));
        params.put("vnp_OrderInfo", "Thanh toán đơn đặt sân " + dto.getId());
        params.put("vnp_Locate", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", getClientIp(request));
        params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(LocalDateTime.now()));
        return paymentUrl + "?" + buildUrl(params, key);
    }

    private String buildUrl(Map<String, String> params, String key) {
        List<String> fields = new ArrayList<>(params.keySet());
        Collections.sort(fields);

        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();

        for (String field : fields) {
            String value = params.get(field);
            if (value != null && !value.isEmpty()) {
                query.append(URLEncoder.encode(field, StandardCharsets.UTF_8))
                        .append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append("&");
                hashData.append(field).append("=").append(value).append("&");
            }
        }

        if (!query.isEmpty()) query.deleteCharAt(query.length() - 1);
        if (!hashData.isEmpty()) hashData.deleteCharAt(hashData.length() - 1);

        String secure = macSHA512(key, hashData.toString());
        return query.append("&vnp_SecureHash=").append(secure).toString();
    }

    public boolean validateSignature(Map<String, String> params, String key) {
        if (params == null || !params.containsKey("vnp_SecureHash")) {
            log.error("Missing vnp_SecureHash in parameters");
            return false;
        }

        String receiveHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");

        List<String> fields = new ArrayList<>(params.keySet());
        Collections.sort(fields);

        StringBuilder hashData = new StringBuilder();
        for (String field : fields) {
            String value = params.get(field);
            if (value != null && !value.isEmpty()) {
                hashData.append(field).append("=").append(value).append("&");
            }
        }

        if (!hashData.isEmpty()) hashData.deleteCharAt(hashData.length() - 1);

        String expectedHash = macSHA512(key, hashData.toString());

        return expectedHash.equalsIgnoreCase(receiveHash);
    }

    private String macSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            log.error("Error generating HMAC SHA512", e);
            throw new RuntimeException("Error generating HMAC SHA512", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexStrings = new StringBuilder();
        for (byte b:bytes) {
            hexStrings.append(String.format("%02X", b));
        }
        return hexStrings.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(value -> value.split(",")[0].trim())
                .filter(i -> !i.isEmpty() && !"unknown".equalsIgnoreCase(i))
                .orElse(request.getRemoteAddr());
    }
}