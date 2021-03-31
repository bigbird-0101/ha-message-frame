package com.fpp.order.service;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.ibatis.type.JdbcType.*;
/**
 * @author fpp
 */
@MappedTypes(String.class)
@MappedJdbcTypes(value = {CLOB,CLOB,VARCHAR,LONGVARCHAR,NVARCHAR,NCHAR,NCLOB},includeNullJdbcType = true)
public class ChineseTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setBytes(i,parameter.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        if(resultSet.getBytes(s)!=null){
            return new String(resultSet.getBytes(s), StandardCharsets.UTF_8);
        }else {
            return null;
        }
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        if(resultSet.getBytes(i)!=null){
            return new String(resultSet.getBytes(i), StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        if(callableStatement.getBytes(i)!=null){
            return new String(callableStatement.getBytes(i), StandardCharsets.UTF_8);
        }else {
            return null;
        }
    }
}