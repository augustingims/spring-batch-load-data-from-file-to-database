package com.teamdevsolution.batch.mappers;

import com.teamdevsolution.batch.domain.Formation;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormationItemPreparedStatementSetter implements ItemPreparedStatementSetter<Formation> {
    @Override
    public void setValues(Formation formation, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, formation.getCode());
        preparedStatement.setString(2, formation.getLibelle());
        preparedStatement.setString(3, formation.getDescriptif());
    }
}
