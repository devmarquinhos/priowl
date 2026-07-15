package com.devmarquinhos.priowl.dto;

public record ResetPasswordRequest(String token, String newPassword) {
}
