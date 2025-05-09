package vn.congdubai.shopping.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
