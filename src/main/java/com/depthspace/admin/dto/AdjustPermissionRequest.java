package com.depthspace.admin.dto;



import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
@Data
public class AdjustPermissionRequest {
    @NotBlank(message = "帳號不可為空")
    private String account;
    private List<AdminAuthorities>  authorities;
}
