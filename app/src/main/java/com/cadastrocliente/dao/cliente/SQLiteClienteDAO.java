package com.cadastrocliente.dao.cliente;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.cadastrocliente.dao.SQLiteDAOFactory;
import com.cadastrocliente.entidade.Cliente;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementa a persistência de cliente utilizando SQLite.
 *
 * @author osmarbraz
 * @version 1.0
 * @updated 19-abr-2020 22:00:00
 */
public class SQLiteClienteDAO extends SQLiteDAOFactory implements ClienteDAO, SQLiteClienteMetaDados {

    /**
     * Retorna uma lista com os objetos segundo o SQL especificado.
     *
     * @param sql String com o SQL a ser executado.
     * @return Lista com os objetos.
     */
    private List select(String sql) {
        List lista = new LinkedList();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getConnection();
            // Executa a consulta no banco de dados
            cursor = db.rawQuery(sql, null);
            // Percorre os dados recuperados
            while (cursor.moveToNext()) {
                Cliente cliente = new Cliente();
                //Recupera o valor do campo pelo índice do nome da coluna
                cliente.setClienteId(cursor.getString(cursor.getColumnIndex(PK[0])));
                cliente.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
                cliente.setCpf(cursor.getString(cursor.getColumnIndex("CPF")));
                lista.add(cliente);
            }
            cursor.close();
            cursor = null;
            db.close();
            db = null;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    ;
                }
                cursor = null;
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
                db = null;
            }
        }
        return lista;
    }

    public boolean incluir(Object obj) {
        if (obj != null) {
            Cliente cliente = (Cliente) obj;
            SQLiteDatabase db = null;
            boolean res = false;
            StringBuilder sql = new StringBuilder();
            try {
                sql.append("insert into " + TABLE + "(");
                sql.append(METADADOSINSERT + " ) ");

                sql.append("values ('" + preparaSQL(cliente.getClienteId()));
                sql.append("','" + preparaSQL(cliente.getNome()));
                sql.append("','" + preparaSQL(cliente.getCpf()) + "')");

                db = getConnection();
                db.execSQL(sql.toString());

                db.close();
                db = null;
                res = true;
            } catch (Exception e) {
                System.out.println(e);
                res = false;
            } finally {
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                    db = null;
                }
            }
            return res;
        }
        return false;
    }

    public int alterar(Object obj) {
        if (obj != null) {
            Cliente cliente = (Cliente) obj;
            SQLiteDatabase db = null;
            int res = 0;
            StringBuilder sql = new StringBuilder();
            try {
                sql.append("update " + TABLE);
                sql.append(" set NOME='" + cliente.getNome() + "',");
                sql.append(" CPF='" + cliente.getCpf() + "'");
                sql.append(" where " + TABLE + "." + PK[0] + "='" + preparaSQL(cliente.getClienteId()) + "'");

                db = getConnection();
                db.execSQL(sql.toString());
                db.close();
                db = null;
                res = 1;

            } catch (Exception e) {
                System.out.println(e);
                res = 0;
            } finally {
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                    db = null;
                }
            }
            return res;
        }
        return 0;
    }

    public int excluir(Object obj) {
        if (obj != null) {
            Cliente cliente = (Cliente) obj;
            SQLiteDatabase db = null;
            StringBuilder sql = new StringBuilder();
            int res = 0;
            try {
                sql.append("delete from " + TABLE + " where " + TABLE + "." + PK[0] + " = '" + preparaSQL(cliente.getClienteId()) + "'");
                db = getConnection();

                db.execSQL(sql.toString());
                db.close();
                db = null;
                res = 1;
            } catch (Exception e) {
                System.out.println(e);
                res = 0;
            } finally {
                if (db != null) {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                    db = null;
                }
            }
            return res;
        }
        return 0;
    }

    /**
     * Monta uma lista com os filtros para consulta de acordo como preenchimento dos atributos do objeto
     *
     * @param obj Objeto que possui os dados do filtro.
     * @return Uma lista com os dados do filtro.
     */
    public List aplicarFiltro(Object obj) {
        if (obj != null) {
            Cliente cliente = (Cliente) obj;

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select " + METADADOSSELECT + " from " + TABLE);

            List filtros = new ArrayList();

            if (cliente.getClienteId() != null && !"".equals(cliente.getClienteId())) {
                filtros.add(TABLE + "." + PK[0] + "='" + preparaSQL(cliente.getClienteId()) + "'");
            }

            if (cliente.getNome() != null && !"".equals(cliente.getNome())) {
                filtros.add(TABLE + ".NOME like upper('%" + preparaSQL(cliente.getNome()) + "%')");
            }

            if (cliente.getCpf() != null && !"".equals(cliente.getCpf())) {
                filtros.add(TABLE + ".CPF = '" + preparaSQL(cliente.getCpf()) + "'");
            }

            if (!filtros.isEmpty()) {
                sqlBuilder.append(" where " + implode(" and ", filtros));
            }

            sqlBuilder.append(" order by " + TABLE + "." + PK[0]);

            return select(sqlBuilder.toString());
        } else {
            return null;
        }
    }

    public void criar() {
        SQLiteDatabase db = null;
        try {
            db = getConnection();
            //Cria a tabela senão existir
            db.execSQL("create table IF NOT EXISTS CLIENTE (CLIENTEID integer, NOME varchar(100), CPF varchar(11), CONSTRAINT PK_CLIENTE PRIMARY KEY (CLIENTEID));");
            db.close();
            db = null;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
                db = null;
            }
        }
    }

    public void apagarTabela() {
        SQLiteDatabase db = null;
        try {
            db = getConnection();
            db.delete(TABLE, null, null);
            db.close();
            db = null;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
                db = null;
            }
        }
    }

    public long getNumeroRegistros() {
        SQLiteDatabase db = getConnection();
        return DatabaseUtils.queryNumEntries(db, TABLE);
    }
}
