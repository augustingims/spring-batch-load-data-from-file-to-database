package com.teamdevsolution.batch.mappers;

import com.teamdevsolution.batch.domain.Seance;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SeanceItemPreparedStatementSetter implements ItemPreparedStatementSetter<Seance> {
    @Override
    public void setValues(Seance seance, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, seance.getCodeFormation());
        preparedStatement.setInt(2, seance.getIdFormateur());
        preparedStatement.setDate(3, Date.valueOf(seance.getDateDebut()));
        preparedStatement.setDate(4, Date.valueOf(seance.getDateFin()));
    }
}
