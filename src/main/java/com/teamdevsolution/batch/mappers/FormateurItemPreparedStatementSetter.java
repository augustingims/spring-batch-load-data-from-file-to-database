package com.teamdevsolution.batch.mappers;

import com.teamdevsolution.batch.domain.Formateur;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormateurItemPreparedStatementSetter implements ItemPreparedStatementSetter<Formateur> {
    @Override
    public void setValues(Formateur formateur, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, formateur.getId());
        preparedStatement.setString(2, formateur.getNom());
        preparedStatement.setString(3, formateur.getPrenom());
        preparedStatement.setString(4, formateur.getAdresseEmail());
    }
}
