package com.schoolchan.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
<<<<<<< HEAD
=======
@org.springframework.test.context.ActiveProfiles("test")
>>>>>>> d5851d3 (Initial commit)
class AnonymityPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicPages_doNotExposeSessionIpOrStaffName() throws Exception {
<<<<<<< HEAD
        for (String path : new String[] {"/", "/rules", "/boards/general"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(not(containsString("sc_session"))))
=======
        for (String path : new String[] {"/", "/rules", "/boards/general", "/boards/general/archive",
                "/boards/general/catalog", "/closed"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(not(containsString("sc_session"))))
                    .andExpect(content().string(not(containsString("sessionHash"))))
>>>>>>> d5851d3 (Initial commit)
                    .andExpect(content().string(not(containsString("127.0.0.1"))))
                    .andExpect(content().string(not(containsString("change-me-now"))));
        }
    }
}
