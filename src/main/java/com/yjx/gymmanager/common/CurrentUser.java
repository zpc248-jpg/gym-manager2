package com.yjx.gymmanager.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {
    private Long userId;
    private String username;
    private String role;
    private Long relatedId;
}
