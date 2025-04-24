package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.congdubai.shopping.service.DashBoardService;
import vn.congdubai.shopping.util.annotation.ApiMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class DashBoardController {
    private final DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/dashboard/count-user-by-day")
    @ApiMessage("Fetch quantity user")
    public ResponseEntity<Long> getCountUserByDay() {
        return ResponseEntity.ok(this.dashBoardService.HandleCountUserByDay());
    }

}
