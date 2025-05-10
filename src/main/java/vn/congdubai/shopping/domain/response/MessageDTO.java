package vn.congdubai.shopping.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

public class MessageDTO {

    private int id;
    private String sender;
    private String receiver;
    private String content;
    @JsonFormat(pattern = "dd/MM/yyy HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date timestamp;

    @JsonProperty("isRead")
    private boolean isRead;

    // Constructor for creating MessageDTO from entity
    public MessageDTO(int id, String sender, String receiver, String content, Date timestamp, boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
}