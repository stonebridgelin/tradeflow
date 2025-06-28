package com.stonebridge.tradeflow.business.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@MappedTypes({String.class})
@MappedJdbcTypes({JdbcType.BIGINT})
public class StringToBigIntTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null || parameter.trim().isEmpty()) {
            ps.setNull(i, Types.BIGINT); // 传 null，启用数据库自增
        } else {
            ps.setLong(i, Long.parseLong(parameter));
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : String.valueOf(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long value = rs.getLong(columnIndex);
        return rs.wasNull() ? null : String.valueOf(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long value = cs.getLong(columnIndex);
        return cs.wasNull() ? null : String.valueOf(value);
    }
}
