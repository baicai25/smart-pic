package org.baicai.smart.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 5322368732045502579L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
