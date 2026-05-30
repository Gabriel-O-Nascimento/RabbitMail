import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api.js";
import Button from "../components/Button.jsx";
import Layout from "../components/Layout.jsx";

const initialForm = {
  subject: "",
  content: "",
};

function SendMessage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((currentForm) => ({ ...currentForm, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (!form.subject.trim()) {
      setError("Informe o assunto do e-mail.");
      return;
    }

    if (!form.content.trim()) {
      setError("Informe o corpo do e-mail.");
      return;
    }

    try {
      setLoading(true);
      await api.post("/messages/send", {
        subject: form.subject.trim(),
        content: form.content.trim(),
      });
      setSuccess("Solicitacao de envio publicada na fila RabbitMQ.");
      setForm(initialForm);
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Nao foi possivel solicitar o envio.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout title="Enviar mensagem em lote">
      <form className="form" onSubmit={handleSubmit}>
        <label>
          Assunto do e-mail
          <input
            type="text"
            name="subject"
            value={form.subject}
            onChange={handleChange}
            placeholder="Comunicado academico"
          />
        </label>

        <label>
          Corpo do e-mail
          <textarea
            name="content"
            value={form.content}
            onChange={handleChange}
            rows="7"
            placeholder="Digite aqui a mensagem que sera enviada em lote."
          />
        </label>

        {success && <p className="message success-message">{success}</p>}
        {error && <p className="message error-message">{error}</p>}

        <Button type="submit" disabled={loading}>
          {loading ? "Enviando..." : "Enviar mensagem"}
        </Button>
      </form>

      <div className="navigation-actions">
        <Button onClick={() => navigate("/listar-emails")} variant="secondary">
          Listar e-mails cadastrados
        </Button>
        <Button onClick={() => navigate("/cadastrar-email")}>Cadastrar novo e-mail</Button>
        <Button onClick={() => navigate("/")} variant="ghost">Voltar para tela inicial</Button>
      </div>
    </Layout>
  );
}

export default SendMessage;
