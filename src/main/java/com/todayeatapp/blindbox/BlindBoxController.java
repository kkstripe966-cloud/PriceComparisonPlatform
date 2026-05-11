package com.todayeatapp.blindbox;

import com.todayeatapp.common.ApiResponse;
import com.todayeatapp.home.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 盲盒抽取控制器
 */
@RestController
@RequestMapping("/api/draw")
public class BlindBoxController {

    @Autowired
    private BlindBoxService blindBoxService;

    /**
     * 随机抽取菜品
     * @param request 抽取请求参数
     * @return 抽取结果
     */
    @PostMapping("/random")
    public ApiResponse<DrawResult> drawRandom(@RequestBody DrawRequest request) {
        try {
            DrawResult result = blindBoxService.drawRandom(request.getMinPrice(), request.getMaxPrice());
            return ApiResponse.success("抽取成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "抽取失败: " + e.getMessage());
        }
    }

    /**
     * 重新抽取菜品
     * @param request 重新抽取请求参数
     * @return 抽取结果
     */
    @PostMapping("/redraw")
    public ApiResponse<DrawResult> redraw(@RequestBody RedrawRequest request) {
        try {
            DrawResult result;
            if (request.getDrawType() == DrawType.PRECISE) {
                // 精准匹配重新抽取
                result = blindBoxService.drawPrecise(
                    request.getBudgetType(), 
                    request.getFlavorTypes(),
                    request.getMinPrice(),
                    request.getMaxPrice()
                );
            } else {
                // 随机重新抽取
                result = blindBoxService.drawRandom(
                    request.getMinPrice() != null ? request.getMinPrice() : 20.0,
                    request.getMaxPrice() != null ? request.getMaxPrice() : 50.0
                );
            }
            return ApiResponse.success("重新抽取成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "重新抽取失败: " + e.getMessage());
        }
    }

    /**
     * 精准匹配抽取
     * @param request 精准抽取请求参数
     * @return 抽取结果
     */
    @PostMapping("/precise")
    public ApiResponse<DrawResult> drawPrecise(@RequestBody PreciseDrawRequest request) {
        try {
            DrawResult result = blindBoxService.drawPrecise(
                request.getBudgetType(), 
                request.getFlavorTypes(),
                request.getMinPrice(),
                request.getMaxPrice()
            );
            return ApiResponse.success("精准抽取成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "精准抽取失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前时间模式
     * @return 时间模式（day/night）
     */
    @GetMapping("/time-mode")
    public ApiResponse<String> getTimeMode() {
        try {
            String timeMode = blindBoxService.getCurrentTimeMode();
            return ApiResponse.success("获取时间模式成功", timeMode);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取时间模式失败: " + e.getMessage());
        }
    }

    /**
     * 获取抽取统计信息
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getDrawStats() {
        try {
            Map<String, Object> stats = blindBoxService.getDrawStats();
            return ApiResponse.success("获取统计信息成功", stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取统计信息失败: " + e.getMessage());
        }
    }
}