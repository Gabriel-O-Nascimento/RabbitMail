import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api.js";
import Button from "../components/Button.jsx";
import Layout from "../components/Layout.jsx";

function EmailList() {
  const navigate = useNavigate();
  const [recipients, setRecipients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadRecipients() {
    try {
      setLoading(true);
      setError("");
      const response = await api.get("/recipients");
      setRecipients(response.data);
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Nao foi possivel carregar os e-mails.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadRecipients();
  }, []);

  return (
    <Layout title="E-mails cadastrados">
      <div className="list-summary">
        <strong>Total de e-mails cadastrados:</strong> {recipients.length}
      </div>

      {loading && <p className="message info-message">Carregando e-mails...</p>}
      {error && <p className="message error-message">{error}</p>}

      {!loading && !error && recipients.length === 0 && (
        <p className="empty-state">Nenhum e-mail cadastrado.</p>
      )}

      {!loading && recipients.length > 0 && (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Nome</th>
                <th>E-mail</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {recipients.map((recipient) => (
                <tr key={recipient.id}>
                  <td>{recipient.name}</td>
                  <td>{recipient.email}</td>
                  <td>
                    <span className={recipient.active ? "status active" : "status inactive"}>
                      {recipient.active ? "Ativo" : "Inativo"}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="navigation-actions">
        <Button onClick={() => navigate("/cadastrar-email")}>Cadastrar novo e-mail</Button>
        <Button onClick={() => navigate("/enviar-mensagem")} variant="success">
          Enviar mensagem para os e-mails cadastrados
        </Button>
        <Button onClick={() => navigate("/")} variant="ghost">Voltar para tela inicial</Button>
      </div>
    </Layout>
  );
}

export default EmailList;
