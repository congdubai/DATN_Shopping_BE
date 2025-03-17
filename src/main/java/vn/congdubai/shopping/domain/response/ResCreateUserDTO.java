package vn.congdubai.shopping.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.congdubai.shopping.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
}
