package it.prova.gestionecompagnia.dao.impiegato;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.prova.gestionecompagnia.dao.AbstractMySQLDAO;
import it.prova.gestionecompagnia.model.Compagnia;
import it.prova.gestionecompagnia.model.Impiegato;

public class ImpiegatoDAOImpl extends AbstractMySQLDAO implements ImpiegatoDAO {
	// la connection stavolta fa parte del this, quindi deve essere 'iniettata'
	// dall'esterno
	public ImpiegatoDAOImpl(Connection connection) {
		super(connection);
	}

	public List<Impiegato> list() throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		ArrayList<Impiegato> result = new ArrayList<Impiegato>();
		Impiegato impiegatoTemp = null;
		Compagnia compagniaTemp = null;

		try (Statement ps = connection.createStatement();
				ResultSet rs = ps
						.executeQuery("SELECT * FROM impiegato i INNER JOIN compagnia c ON i.id_compagnia = c.id;")) {

			while (rs.next()) {
				impiegatoTemp = new Impiegato();
				impiegatoTemp.setNome(rs.getString("i.nome"));
				impiegatoTemp.setCognome(rs.getString("i.cognome"));
				impiegatoTemp.setCodiceFiscale(rs.getString("i.codiceFiscale"));
				impiegatoTemp.setDataNascita(rs.getDate("i.dataNascita"));
				impiegatoTemp.setDataAssunzione(rs.getDate("i.dataAssunzione"));
				impiegatoTemp.setId(rs.getLong("i.id_compagnia"));

				compagniaTemp = new Compagnia();
				compagniaTemp.setId(rs.getLong("c.id"));
				compagniaTemp.setRagioneSociale(rs.getString("c.ragioneSociale"));
				compagniaTemp.setFattualeAnnuo(rs.getInt("c.fattualeAnnuo"));
				compagniaTemp.setDataFondazione(rs.getDate("c.dataFondazione"));

				impiegatoTemp.setCompagnia(compagniaTemp);
				result.add(impiegatoTemp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public Impiegato get(Long idInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (idInput == null || idInput < 1)
			throw new Exception("Valore di input non ammesso.");

		Impiegato result = null;
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM impiegato WHERE id = ?")) {

			ps.setLong(1, idInput);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					result = new Impiegato();
					result.setId(rs.getLong("id"));
					result.setNome(rs.getString("nome"));
					result.setCognome(rs.getString("cognome"));
					result.setCodiceFiscale(rs.getString("codiceFiscale"));
					result.setDataNascita(rs.getDate("dataNascita"));
					result.setDataAssunzione(rs.getDate("dataAssunzione"));
					result.setId(rs.getLong("id_compagnia"));
				} else {
					result = null;
				}
			} // niente catch qui

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int insert(Impiegato impiegatoInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (impiegatoInput == null)
			throw new Exception("Valore di input non ammesso.");

		int result = 0;
		try (PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO impiegato (nome, cognome, codiceFiscale, dataNascita, dataAssunzione, id_compagnia) VALUES (?, ?, ?, ?, ?, ?);")) {
			ps.setString(1, impiegatoInput.getNome());
			ps.setString(2, impiegatoInput.getCognome());
			ps.setString(3, impiegatoInput.getCodiceFiscale());
			ps.setDate(4, new java.sql.Date(impiegatoInput.getDataNascita().getTime()));
			ps.setDate(5, new java.sql.Date(impiegatoInput.getDataAssunzione().getTime()));
			ps.setLong(6, impiegatoInput.getCompagnia().getId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int update(Impiegato impiegatoInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (impiegatoInput == null || impiegatoInput.getId() == null || impiegatoInput.getId() < 1)
			throw new Exception("Valore di input non ammesso.");

		int result = 0;
		try (PreparedStatement ps = connection.prepareStatement(
				"UPDATE impiegato SET nome = ?, cognome = ?, codiceFiscale = ?, dataNascita = ?, dataAssunzione=?, id_compagnia = ? WHERE id=?;")) {
			ps.setString(1, impiegatoInput.getNome());
			ps.setString(2, impiegatoInput.getCognome());
			ps.setString(3, impiegatoInput.getCodiceFiscale());
			ps.setDate(4, new java.sql.Date(impiegatoInput.getDataNascita().getTime()));
			ps.setDate(5, new java.sql.Date(impiegatoInput.getDataAssunzione().getTime()));
			ps.setLong(6, impiegatoInput.getCompagnia().getId());
			ps.setLong(7, impiegatoInput.getId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int delete(Impiegato impiegatoInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (impiegatoInput == null || impiegatoInput.getId() == null || impiegatoInput.getId() < 1)
			throw new Exception("Valore di input non ammesso.");

		int result = 0;
		try (PreparedStatement ps = connection.prepareStatement("DELETE FROM impiegato WHERE id = ?")) {
			ps.setLong(1, impiegatoInput.getId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public List<Impiegato> findByExample(Impiegato input) throws Exception {
		// TODO Auto-generated method stub

		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (input == null)
			throw new Exception("Valore di input non ammesso.");

		ArrayList<Impiegato> result = new ArrayList<Impiegato>();
		Impiegato impiegatoTemp = null;

		String query = "SELECT * FROM user WHERE 1=1 ";
		if (input.getCognome() != null && !input.getCognome().isEmpty()) {
			query += " AND cognome like '" + input.getCognome() + "%' ";
		}
		if (input.getNome() != null && !input.getNome().isEmpty()) {
			query += " AND nome like '" + input.getNome() + "%' ";
		}
		if (input.getDataNascita() != null) {
			query += " AND dateaNascita = '" + new java.sql.Date(input.getDataNascita().getTime()) + "' ";
		}
		if (input.getDataAssunzione() != null) {
			query += " AND dataAssunzione = '" + new java.sql.Date(input.getDataAssunzione().getTime()) + "' ";
		}

		try (Statement ps = connection.createStatement()) {
			ResultSet rs = ps.executeQuery(query);

			while (rs.next()) {
				impiegatoTemp = new Impiegato();
				impiegatoTemp.setNome(rs.getString("nome"));
				impiegatoTemp.setCognome(rs.getString("cognome"));
				impiegatoTemp.setCodiceFiscale(rs.getString("codiceFiscale"));
				impiegatoTemp.setDataNascita(rs.getDate("dataNascita"));
				impiegatoTemp.setDataAssunzione(rs.getDate("dataAssunzione"));

				result.add(impiegatoTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;

	}

	@Override
	public List<Impiegato> findAllByCompagnia(Compagnia compagniaInput) throws Exception {
		// TODO Auto-generated method stub
		List<Impiegato> result = new ArrayList<Impiegato>();

		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (compagniaInput == null || compagniaInput.getId() == null || compagniaInput.getId() < 1)
			throw new Exception("Valore di input non ammesso.");

		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM impiegato i INNER JOiN compagnia c ON i.id_compagnia = c.id WHERE c.ragioneSociale = ? ;")) {

			ps.setString(1, compagniaInput.getRagioneSociale());
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Impiegato impiegatoTemp = new Impiegato();
					impiegatoTemp.setId(rs.getLong("i.id"));
					impiegatoTemp.setNome(rs.getString("i.nome"));
					impiegatoTemp.setCognome(rs.getString("cognome"));
					impiegatoTemp.setCodiceFiscale(rs.getString("codiceFiscale"));
					impiegatoTemp.setDataNascita(rs.getDate("dataNascita"));
					impiegatoTemp.setDataAssunzione(rs.getDate("dataAssunzione"));
					result.add(impiegatoTemp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	@Override
	public int countByDataFondazioneCompagniaGreaterThan(java.util.Date dataInput) throws Exception {
		// TODO Auto-generated method stub
		int count = 0;

		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (dataInput == null)
			throw new RuntimeException("Valore di input non valido");

		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT i.* FROM impiegato i INNER JOIN compagnia c ON i.id_compagnia = c.id WHERE c.dataFondazione > ? ;")) {

			ps.setDate(1, new java.sql.Date(dataInput.getTime()));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					count++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return count;
	}

	@Override
	public List<Impiegato> findAllByCompagniaConFatturatoMaggioreDi(int fatturatoInput) throws Exception {
		// TODO Auto-generated method stub
		List<Impiegato> result = new ArrayList<Impiegato>();

		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (fatturatoInput < 0)
			throw new RuntimeException("Valore di input non valido");

		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT i.* FROM impiegato i INNER JOIN compagnia c ON i.id_compagnia = c.id WHERE c.fattualeAnnuo > ? ;")) {

			ps.setInt(1, fatturatoInput);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Impiegato impiegatoTemp = new Impiegato();
					impiegatoTemp.setId(rs.getLong("i.id"));
					impiegatoTemp.setNome(rs.getString("i.nome"));
					impiegatoTemp.setCognome(rs.getString("cognome"));
					impiegatoTemp.setCodiceFiscale(rs.getString("codiceFiscale"));
					impiegatoTemp.setDataNascita(rs.getDate("dataNascita"));
					impiegatoTemp.setDataAssunzione(rs.getDate("dataAssunzione"));

					result.add(impiegatoTemp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	@Override
	public List<Impiegato> findAllErroriAssunzione() throws Exception {
		// TODO Auto-generated method stub
		List<Impiegato> result = new ArrayList<Impiegato>();

		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM impiegato i INNER JOIN compagnia c ON i.id_compagnia = c.id WHERE  i.dataAssunzione < c.dataFondazione;")) {

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Impiegato impiegatoTemp = new Impiegato();
					impiegatoTemp.setId(rs.getLong("i.id"));
					impiegatoTemp.setNome(rs.getString("i.nome"));
					impiegatoTemp.setCognome(rs.getString("cognome"));
					impiegatoTemp.setCodiceFiscale(rs.getString("codiceFiscale"));
					impiegatoTemp.setDataNascita(rs.getDate("dataNascita"));
					impiegatoTemp.setDataAssunzione(rs.getDate("dataAssunzione"));

					result.add(impiegatoTemp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	@Override
	public boolean impiegatiPresentiInCompagnia(Long compagniaDaElimiare) throws Exception {
		// TODO Auto-generated method stub

		List<Impiegato> result = new ArrayList<Impiegato>();

		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT i.* FROM impiegato i INNER JOIN compagnia c ON i.id_compagnia = c.id WHERE c.id = ?;")) {
			ps.setLong(1, compagniaDaElimiare);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Impiegato impiegatoTemp = new Impiegato();
					impiegatoTemp.setId(rs.getLong("id"));
					impiegatoTemp.setNome(rs.getString("nome"));
					impiegatoTemp.setCognome(rs.getString("cognome"));
					impiegatoTemp.setCodiceFiscale(rs.getString("codiceFiscale"));
					impiegatoTemp.setDataNascita(rs.getDate("dataNascita"));
					impiegatoTemp.setDataAssunzione(rs.getDate("dataAssunzione"));
					impiegatoTemp.setId(rs.getLong("id_compagnia"));

					result.add(impiegatoTemp);
				}
			} // niente catch qui

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		if (result.size() > 0) {
			return true;
		}

		return false;
	}

}
