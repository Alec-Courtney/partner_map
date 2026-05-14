package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.ChatRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.ChatRoom;

import java.util.List;

public class MyChatsViewModel extends ViewModel {
    private final ChatRepository chatRepository;

    public MyChatsViewModel() {
        chatRepository = new ChatRepository();
    }

    public LiveData<Resource<List<ChatRoom>>> loadChatRooms() {
        return chatRepository.getChatRooms();
    }
}
