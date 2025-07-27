package com.atnjupt.sqyxgo.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atnjupt.sqyxgo.common.constant.RedisConst;
import com.atnjupt.sqyxgo.common.exception.SqyxgoException;
import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.common.result.ResultCodeEnum;
import com.atnjupt.sqyxgo.common.security.AuthContextHolder;
import com.atnjupt.sqyxgo.common.utils.helper.JwtHelper;
import com.atnjupt.sqyxgo.enums.UserType;
import com.atnjupt.sqyxgo.model.user.User;
import com.atnjupt.sqyxgo.user.Service.UserService;
import com.atnjupt.sqyxgo.user.utils.ConstantPropertiesUtil;
import com.atnjupt.sqyxgo.user.utils.HttpClientUtils;
import com.atnjupt.sqyxgo.vo.user.LeaderAddressVo;
import com.atnjupt.sqyxgo.vo.user.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:WeixinApiController
 * Package: com.atnjupt.sqyxgo.user.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/24 11:18
 * @Version 1.0
 */
@RestController
@Api(tags = "微信登录接口测试")
@RequestMapping("/api/user/weixin/wxLogin")
@RequiredArgsConstructor
@Validated
public class WeixinApiController {

    private final UserService userService;
    private final RedisTemplate redisTemplate;


    //微信用户授权登录
    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("{code}")
    public Result weLogin(@PathVariable(value = "code") @NotEmpty String code){


        //1 前端登录会返回给后端一个临时票据code
        //2 拿着code appid app密钥去微信接口服务换取sessionKey和openId
        ////2.1 拿到工具类中的appid和app密钥
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        ////2.2 先拼接请求地址
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");
        ////2.3 填参数
        String wxAccessUrl = String.format(baseAccessTokenUrl.toString(),wxOpenAppId, wxOpenAppSecret, code);

        //3 请求微信接口服务，返回两个值session_key和openId
        ////通过HttpClient去微信服务器换取sessionKeu和openId
        ////openId 是微信用户在某个公众号、小程序等特定应用下的唯一标识，但它不是全平台唯一的
        String wxReturnInfo = null;
        try {
            wxReturnInfo = HttpClientUtils.get(wxAccessUrl);
        } catch (Exception e){
            throw new SqyxgoException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //4 添加微信用户信息到数据库里面
        ////4.1 拿到openid和session_key
        JSONObject jsonObject = JSONObject.parseObject(wxReturnInfo);
        if (jsonObject.getString("errcode") != null) {
            throw new SqyxgoException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        String openid = jsonObject.getString("openid");
        String session_key = jsonObject.getString("session_key");
        ////4.2 操作user表
        ////判断是否是第一次使用微信授权登录：如何判断？openId
        ////如果第一次登录，则把信息加入到数据库
        User user = userService.getUserByOpenId(openid);
        if (user == null){
            user = new User();
            user.setIsNew(0);
            user.setNickName(openid);
            user.setOpenId(openid);
            user.setUserType(UserType.USER);
            userService.save(user);
            System.out.println("userId = " + user.getId());
        }
        //5 根据userId查询提货点和团长信息
        ////提货点   user表 user_delivery表
        ////团长    leader表
        ////5.1 通过userId拿到Leader信息，封装到LeaderAddressVo里面
        LeaderAddressVo leaderAddressVo = userService.getLeaderAddressVoByUserId(user.getId());
        //6 使用JwT工具根据userId和userName生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getNickName());
        //7 获取当前登录用户信息封装到UserLoginVo，放到Redis里面，设置有效时间
        UserLoginVo userLoginVo = userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(),userLoginVo,3, TimeUnit.DAYS);
        //8 需要数据封装到map返回
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);
        map.put("leaderAddressVo",leaderAddressVo);
        map.put("token",token);
        return Result.ok(map);
    }


    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        User user1 = userService.getById(AuthContextHolder.getUserIdThreadLocal());
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }






}
