package com.itheima.test;

import com.tanhua.model.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class StreamTest {
    public static void main(String[] args) {
        List<UserDTO> dtos = new ArrayList<>();
        /*for (int i = 0; i < 10; i++) {
            UserDTO dto = new UserDTO();
            dto.setPhone(""+i+i+i+i+i+i+i+i+i+i);
            dtos.add(dto);
        }
        for (UserDTO dto : dtos) {
            System.out.println(dto);
        }
        dtos = null;*/
        dtos.stream().forEach(dto -> System.out.println(dto));
    }
}
