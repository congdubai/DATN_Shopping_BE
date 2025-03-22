package vn.congdubai.shopping.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.congdubai.shopping.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String avatar;
    private String address;
    private int age;
    private Instant updatedAt;
}
