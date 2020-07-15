package cn.jboost.springboot.autoconfig.tkmapper.typehandlers;

import cn.hutool.json.JSONUtil;
import cn.jboost.springboot.autoconfig.tkmapper.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * for postgreSql jsonb db type
 */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(String.class)
public class JsonbTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        String jsonString = JSONUtil.toJsonStr(parameter);
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(jsonString);
        ps.setObject(i, jsonObject);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String jsonString = rs.getString(columnName);
        return JsonUtil.parseJson(jsonString);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        return JsonUtil.parseJson(jsonString);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {

        String jsonString = cs.getString(columnIndex);
        return JsonUtil.parseJson(jsonString);

    }


}
