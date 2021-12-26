package com.itheima.test;

import com.tanhua.model.domain.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class StreamTest {
    public static void main(String[] args) {
        List<UserDto> dtos = new ArrayList<>();
        /*for (int i = 0; i < 10; i++) {
            UserDto dto = new UserDto();
            dto.setPhone(""+i+i+i+i+i+i+i+i+i+i);
            dtos.add(dto);
        }
        for (UserDto dto : dtos) {
            System.out.println(dto);
        }
        dtos = null;*/
        dtos.stream().forEach(dto -> System.out.println(dto));
    }
}
