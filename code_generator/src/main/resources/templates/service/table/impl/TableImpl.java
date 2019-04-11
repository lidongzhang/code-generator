package app.api.service.{t}.impl;

import app.api.service.{t}.{T}Interface;

import app.api.service.util.DataTypeEnum;
import app.api.service.util.Page;
import app.api.service.util.Validation;
import app.conf.ApiResponse.Response;
import app.conf.ApiResponse.ResponseData;
import app.conf.ApiResponse.ResponsePageData;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Map;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import app.home.controller.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Scope("prototype")
public class {T}Impl implements {T}Interface {

    @Autowired
    private app.home.controller.AppProperties appProperties;

    @Resource
    private {mybatisPack}.dao.{T}Mapper {t}Mapper;

    @Override
    public Response getByPrimaryKey(Long id){
        ResponseData r = Validation.checkId(id);
        if (r != null) return r;
        {setlid}
        return ResponseData.success({t}Mapper.selectByPrimaryKey(lid));
    }

    @Override
    public Response updateByPrimaryKeyChanged({mybatisPack}.entity.{T} {t}Changed){
        ResponseData r = checkData({t}Changed);
        if(r.getCode().equals(Response.CODE_FAIL)) return r;
        return ResponseData.success({t}Mapper.updateByPrimaryKeyChanged({t}Changed));
    }

    @Override
    public Response insertChanged({mybatisPack}.entity.{T} {t}Changed){
        ResponseData r = checkData({t}Changed);
        if(r.getCode().equals(Response.CODE_FAIL)) return r;

        if(appProperties.getDatabaseType() == AppProperties.DatabaseType.MYSQL)
            {t}Mapper.insertChanged({t}Changed);
        if(appProperties.getDatabaseType() == AppProperties.DatabaseType.SQLSERVER)
            {t}Mapper.insertSqlserverChanged({t}Changed);

        //{t}Mapper.insertChanged({t}Changed);
        return ResponseData.success({t}Changed.get{Id}());
    }

    @Override
    public Response deleteByPrimaryKey(Long id){
        ResponseData r = Validation.checkId(id);
        if (r != null) return r;
        {setlid}
        return ResponseData.success({t}Mapper.deleteByPrimaryKey(lid));
    }

    @Override
    public Response selectByMap(Map<String, Object> map)throws Exception{
        {mybatisPack}.entity.{T}Example {t}Example = new {mybatisPack}.entity.{T}Example();
        //add condition
        ResponseData r = checkSelectDataAddExample(map, {t}Example);
        if(r.getCode().equals(Response.CODE_FAIL)) return r;
        return ResponseData.success({t}Mapper.selectByExample({t}Example));
    }

    @Override
    public Response selectPageByMap(Map<String, Object> map)throws Exception{
        {mybatisPack}.entity.{T}Example {t}Example = new {mybatisPack}.entity.{T}Example();
        //add condition
        ResponseData r = checkSelectDataAddExample(map, {t}Example);
        if(r.getCode().equals(Response.CODE_FAIL)) return r;

//        Long count = {t}Mapper.countByExample({t}Example);
//        Integer limit = Integer.parseInt(map.get("limit").toString());
//        Integer offset = Page.getOffset(limit, Integer.parseInt(map.get("page").toString()));
//        {t}Example.setOffset(offset);
//        {t}Example.setLimit(limit);
//        Object data = {t}Mapper.selectByExample({t}Example);

        //page

        String orderBy = map.get("orderBy").toString();
        if(orderBy == null || orderBy.isEmpty())
        return ResponseData.fail("orderBy 参数必须设置");

        Long count = {t}Mapper.countByExample({t}Example);

        Integer limit = Integer.parseInt(map.get("limit").toString());//每页数据量
        Integer page = Integer.parseInt(map.get("page").toString());//页码的参数
        Integer offset = Page.getOffset(limit, page);

        Integer rowidStart = Page.getRowidStart(limit, page);
        Integer rowidEnd = Page.getRowidEnd(limit, page);

        Object data = null;
        if(appProperties.getDatabaseType() == AppProperties.DatabaseType.MYSQL) {
        {t}Example.setOffset(offset);
        {t}Example.setLimit(limit);
        data = {t}Mapper.selectByExample({t}Example);
        }
        if(appProperties.getDatabaseType() == AppProperties.DatabaseType.SQLSERVER){
        {t}Example.setOrderByClause(orderBy);
        {t}Example.setRowidStart(rowidStart);
        {t}Example.setRowidEnd(rowidEnd);
        data = {t}Mapper.selectSqlserverByExample({t}Example);
        }

        return ResponsePageData.success(data, count);
    }

    private ResponseData checkSelectDataAddExample(Map<String, Object> map, {mybatisPack}.entity.{T}Example {t}Example)
        throws Exception
        {
        boolean b = true;
        ResponseData r ;
        StringBuilder sb = new StringBuilder();

        {mybatisPack}.entity.{T}Example.Criteria c = {t}Example.createCriteria();

//        Object c1 = map.get("c1");
//        r = Validation.check("c1", DataTypeEnum.STRING, c1, false, 0L, 100L, 0);
//        if(r.getCode().equals(Response.CODE_FAIL)){
//            b = false;
//            sb.append(r.getMsg());
//        }else{
//            if(c1 != null && !(c1.toString().isEmpty()) )
//                c.andC1Like(String.format("%%%s%%", c1.toString()));
//        }
//
//        Object c2 = map.get("c2");
//        r = Validation.check("c2", DataTypeEnum.INT, c2, false, 0L, 100L, 0);
//        if(r.getCode().equals(Response.CODE_FAIL)){
//            b = false;
//            sb.append(String.format("/r/n%s", r.getMsg()));
//        }else{
//            if(c2 != null && !c2.toString().isEmpty())
//                c.andC2EqualTo(Integer.parseInt(c2.toString()));
//        }
        {SelectDataAddExampleColumns}
        if(!b)
            return ResponseData.fail(sb.toString());

        return ResponseData.success("");
    }

    //region checkData
    private ResponseData checkData({mybatisPack}.entity.{T} {t}Changed)
        {

        StringBuilder sb = new StringBuilder();
        ResponseData r;
        boolean b = true;

//        if(t1Changed.getC1_changed()){
//            r = Validation.check("c1", DataTypeEnum.STRING, t1Changed.getC1(),
//                    true, 1L,10L, 0);
//            if(r.getCode().equals(Response.CODE_FAIL)){
//                b = false;
//                sb.append(String.format("%s\r\n", r.getMsg()));
//            }
//        }
//        if(t1Changed.getC2_changed()){
//            r = Validation.check("c1", DataTypeEnum.STRING, t1Changed.getC2(),
//                    true, 1L,10L, 0);
//            if(r.getCode().equals(Response.CODE_FAIL)){
//                b = false;
//                sb.append(String.format("%s\r\n", r.getMsg()));
//            }
//        }
        {checkDataColumns}
        if(!b)
            return ResponseData.fail(sb.toString());
        else
            return ResponseData.success(null);
    }
    //endregion

}
