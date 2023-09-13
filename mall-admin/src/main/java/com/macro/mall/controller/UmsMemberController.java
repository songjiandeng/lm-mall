package com.macro.mall.controller;


import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsChannelMemberVo;
import com.macro.mall.dto.UmsMemberDetail;
import com.macro.mall.dto.UmsMemberParamDto;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "用户中心管理")
@Tag(name = "UmsMemberController", description = "用户中心管理")
@RequestMapping("/member")
@RequiredArgsConstructor
public class UmsMemberController {

    @Autowired
    private UmsMemberService umsMemberService;

    @ApiOperation("客户端用户注册（渠道，自然用户）")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult register(@RequestBody UmsMemberParamDto umsMemberParamDto) {
        int register = umsMemberService.register(umsMemberParamDto);
        if(register>0){
            return CommonResult.success(null,"注册成功");
        }
        return CommonResult.failed();
    }

    @ApiOperation("根据头部条件检索用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<UmsMember>> list(@RequestBody UmsMemberParamDto umsMemberParamDto) {
        List<UmsMember> memberList = umsMemberService.list(umsMemberParamDto);
        return CommonResult.success(CommonPage.restPage(memberList));
    }

    @ApiOperation("备注，批量备注")
    @RequestMapping(value = "/batchUpdateMark", method = RequestMethod.POST)
    public CommonResult batchUpdateMark(@RequestBody UmsMemberParamDto umsMemberParamDto) {
        int count = umsMemberService.batchUpdateMark(umsMemberParamDto);
        return CommonResult.success(count);
    }

    @ApiOperation("归属，批量归属")
    @RequestMapping(value = "/batchUpdateAdmin", method = RequestMethod.POST)
    public CommonResult batchUpdateAdmin(@RequestBody UmsMemberParamDto umsMemberParamDto) {
        int count = umsMemberService.batchUpdateAdmin(umsMemberParamDto);
        return CommonResult.success(count);
    }

    @ApiOperation("分组，批量分组")
    @RequestMapping(value = "/batchUpdateRole", method = RequestMethod.POST)
    public CommonResult batchUpdateRole(@RequestBody UmsMemberParamDto umsMemberParamDto) {
        int count = umsMemberService.batchUpdateRole(umsMemberParamDto);
        return CommonResult.success(count);
    }

    @ApiOperation("修改指定用户信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult update(@PathVariable Long id, @RequestBody UmsMember umsMember) {
        int count = umsMemberService.update(id, umsMember);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CommonResult<UmsMemberDetail> getItem(@PathVariable Long id) {
        UmsMemberDetail umsMember = umsMemberService.getItem(id);
        return CommonResult.success(umsMember);
    }

    @ApiOperation("渠道用户列表检索")
    @RequestMapping(value = "/channel/list", method = RequestMethod.POST)
    public CommonResult<UmsChannelMemberVo> channelList(@RequestBody UmsMemberParamDto umsMemberParamDto) {
        List<UmsMember> memberList = umsMemberService.channelList(umsMemberParamDto);
        UmsChannelMemberVo vo = new UmsChannelMemberVo();
        CommonPage<UmsMember> umsMemberVoCommonPage = CommonPage.restPage(memberList);
        vo.setMemberVoList(umsMemberVoCommonPage);
        if(memberList.isEmpty()){
            vo.setChannelUserNum(0);
            vo.setOrderUserNum(0);
            return CommonResult.success(vo);
        }else{
            vo.setChannelUserNum(memberList.size());
            long count = memberList.stream().filter(f -> f.getOrderNum() > 0).count();
            vo.setOrderUserNum(Integer.parseInt(count+""));
        }
        return CommonResult.success(vo);
    }

}
