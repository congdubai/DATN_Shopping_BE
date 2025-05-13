package vn.congdubai.shopping.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Messages;
import vn.congdubai.shopping.domain.response.MessageDTO;
import vn.congdubai.shopping.repository.MessageRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    private final ModelMapper modelMapper = new ModelMapper();

    public List<MessageDTO> getMessagesByReceiver(String receiver) {
        List<Messages> messages = this.messageRepository.findByReceiverOrderByTimestampDesc(receiver);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void saveMessage(Messages message) {
        messageRepository.save(message);
    }

    public void markAsRead(int id) {
        Optional<Messages> message = this.messageRepository.findById(id);
        if (message.isPresent()) {
            Messages mes = message.get();
            mes.setRead(true);
            messageRepository.save(mes);
        }
    }

    public long getUnreadMessageCount(String receiver) {
        return this.messageRepository.countByReceiverAndIsRead(receiver, false);
    }

    public List<MessageDTO> getAllMessages() {
        return this.messageRepository.findAll().stream().map(r -> convertToDTO(r)).collect(Collectors.toList());
    }

    private MessageDTO convertToDTO(Messages message) {
        return modelMapper.map(message, MessageDTO.class);
    }
}
