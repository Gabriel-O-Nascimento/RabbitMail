import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api.js";
import Button from "../components/Button.jsx";
import Layout from "../components/Layout.jsx";

const initialForm = {
  name: "",
  email: "",
};

function RegisterEmail() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((currentForm) => ({ ...currentForm, [name]: value }));
  }

  function clearForm() {
    setForm(initialForm);
    setError("");
    setSuccess("");
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (!form.name.trim()) {
      setError("Informe o nome do destinatario.");
      return;
    }

    if (!form.email.trim()) {
      setError("Informe o e-mail do destinatario.");
      return;
    }

    try {
      setLoading(true);
      await api.post("/recipients", {
        name: form.name.trim(),
        email: form.email.trim(),
      });
      setSuccess("E-mail cadastrado com sucesso.");
      setForm(initialForm);
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Nao foi possivel cadastrar o e-mail.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout title="Cadastrar novo e-mail">
      <form className="form" onSubmit={handleSubmit}>
        <label>
          Nome
          <input
            type="text"
            name="name"
            value={form.name}
            onChange={handleChange}
            placeholder="Ana Souza"
          />
        </label>

        <label>
          E-mail
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="ana.souza@example.com"
          />
        </label>

        {success && <p className="message success-message">{success}</p>}
        {error && <p className="message error-message">{error}</p>}

        <Button type="submit" disabled={loading}>
          {loading ? "Cadastrando..." : "Cadastrar"}
        </Button>
      </form>

      <div className="navigation-actions">
        <Button onClick={clearForm} variant="secondary">Realizar outro cadastro</Button>
        <Button onClick={() => navigate("/listar-emails")} variant="secondary">
          Listar e-mails cadastrados
        </Button>
        <Button onClick={() => navigate("/enviar-mensagem")} variant="success">
          Enviar mensagem para os e-mails cadastrados
        </Button>
        <Button onClick={() => navigate("/")} variant="ghost">Voltar para tela inicial</Button>
      </div>
    </Layout>
  );
}

export default RegisterEmail;
