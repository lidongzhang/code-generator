package app.api.service.{t};

import app.conf.ApiResponse.Response;
import java.util.Map;

public interface {T}Interface {

    //selectByPrimaryKey
    Response getByPrimaryKey(Long id);

    //updateChanged
    Response updateByPrimaryKeyChanged({mybatisPack}.entity.{T} {t}Changed);

    //insertChanged
    Response insertChanged({mybatisPack}.entity.{T} {t}Changed);

    //deleteByPrimaryKey
    Response deleteByPrimaryKey(Long id);

    //selectByExample
    Response selectByMap(Map<String, Object> map)throws Exception;

    Response selectPageByMap(Map<String, Object> map)throws Exception;

}
