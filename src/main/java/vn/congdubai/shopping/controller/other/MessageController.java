package vn.congdubai.shopping.controller.other;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import vn.congdubai.shopping.domain.response.MessageDTO;
import vn.congdubai.shopping.service.MessageService;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages/{username}")
    public List<MessageDTO> getMessagesByUser(@PathVariable("username") String username) {
        return messageService.getMessagesByReceiver(username);
    }

    @PutMapping("/messages/mark-as-read/{id}")
    public void markMessageAsRead(@PathVariable("id") int id) {
        messageService.markAsRead(id);
    }

    @GetMapping("/unread-count/{receiver}")
    public long getUnreadMessageCount(@PathVariable("receiver") String receiver) {
        return messageService.getUnreadMessageCount(receiver);
    }
}