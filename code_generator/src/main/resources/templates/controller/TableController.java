package app.api.controller;

import app.api.service.{t}.{T}Interface;
import app.mybatis.entity.{T};

import app.conf.ApiResponse.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Scope("prototype")
@RequestMapping(value = "/api/{t}")
public class {T}Controller {

    @Autowired
    private {T}Interface {t}Interface;

    private static class GetByPrimaryKeyParam{
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        private Long id;
    }
    @RequestMapping("/getByPrimaryKey")
    public Response getByPrimaryKey(@RequestBody GetByPrimaryKeyParam param){
        return {t}Interface.getByPrimaryKey(param.getId());
    }

    @RequestMapping("/updateByPrimaryKeyChanged")
    public Response updateByPrimaryKeyChanged(@RequestBody {T} {t}Changed){
        return {t}Interface.updateByPrimaryKeyChanged({t}Changed);
    }

    @RequestMapping("/insertChanged")
    public Response insertChanged(@RequestBody {T} {t}Changed){
        return {t}Interface.insertChanged({t}Changed);
    }

    private static class DeleteByPrimaryKeyParam{
        private Long id;
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
    @RequestMapping("/deleteByPrimaryKey")
    public Response deleteByPrimaryKey(@RequestBody DeleteByPrimaryKeyParam param){
        return {t}Interface.deleteByPrimaryKey(param.getId());
    }

    @RequestMapping("/selectPageByMap")
    public Response selectPageByMap(@RequestBody Map<String,Object> map) throws  Exception{
        return {t}Interface.selectPageByMap(map);
    }
}
