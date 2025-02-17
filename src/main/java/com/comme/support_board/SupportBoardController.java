package com.comme.support_board;

import com.comme.files.FileService;
import com.comme.member.MemberDTO;
import com.comme.vol.VolBoardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/supportBoard")
public class SupportBoardController {
    @Autowired
    private SupportBoardService supportBoardService;
    @Autowired
    private FileService fileService;
    @Autowired
    private HttpSession httpSession;

    Logger logger = LoggerFactory.getLogger(VolBoardController.class);

    @GetMapping("/lists")
    public String volBoard(@RequestParam(value = "curPage", defaultValue = "1") int curPage, Model model) throws Exception {
        int start = 1;
        int end = 12;
        List<Map<String, Object>> list = supportBoardService.selectList(start, end);
        model.addAttribute("list", list);
        return "support/support_board_list";
    }

    @GetMapping("/write")
    public String write(Model model) throws Exception {
        int total = supportBoardService.selectTotalCnt();
        List<Map<String, Object>> list = supportBoardService.selectList(total-1,total);
        model.addAttribute("list", list);
        return "support/support_board_write";
    }

    @PostMapping("/write")
    public String wrtie(SupportBoardDTO supportBoardDTO, String bank_category, @RequestParam(value = "files_name") List<String> files_name, @RequestParam(value = "temp_files[]") String[] temp_files) throws Exception {
        String path = httpSession.getServletContext().getRealPath("");
        supportBoardDTO.setSupport_bank(bank_category+ " " +supportBoardDTO.getSupport_bank());
        supportBoardDTO.setMember_id(((MemberDTO)httpSession.getAttribute("loginSession")).getMember_id());
        supportBoardDTO.setWriter_nickname(((MemberDTO)httpSession.getAttribute("loginSession")).getMember_id());
        supportBoardDTO.setMember_brn(((MemberDTO)httpSession.getAttribute("loginSession")).getMember_brn());
        logger.info(supportBoardDTO.toString());

        int seq_board = supportBoardService.insert(supportBoardDTO);

        fileService.insert_file(seq_board,files_name,temp_files,path,"support_files");

        return "redirect:/supportBoard/lists";
    }
    @GetMapping("/view")
    public String detail(@RequestParam(value = "seq_board") int seq_board, Model model) throws Exception {

        Map<String, Object> map = supportBoardService.select(seq_board);
        model.addAttribute("map", map);


        int total =supportBoardService.selectTotalCnt();
        List<Map<String, Object>> list = supportBoardService.selectList(total-1,total);
        model.addAttribute("list", list);

        return "support/support_board_detail";
    }
}
