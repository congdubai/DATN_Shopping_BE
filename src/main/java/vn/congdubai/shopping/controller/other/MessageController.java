package vn.congdubai.shopping.controller.other;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import vn.congdubai.shopping.domain.response.MessageDTO;
import vn.congdubai.shopping.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/message/{username}")
    public List<MessageDTO> getMessagesByUser(@PathVariable("username") String username) {
        return messageService.getMessagesByReceiver(username);
    }

    @PostMapping("/mark-as-read/{id}")
    public void markMessageAsRead(@PathVariable("id") int id) {
        messageService.markAsRead(id);
    }

    @GetMapping("/unread-count/{receiver}")
    public long getUnreadMessageCount(@PathVariable("receiver") String receiver) {
        return messageService.getUnreadMessageCount(receiver);
    }
}