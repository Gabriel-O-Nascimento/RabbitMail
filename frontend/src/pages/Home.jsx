import { useNavigate } from "react-router-dom";
import Button from "../components/Button.jsx";
import Layout from "../components/Layout.jsx";

function Home() {
  const navigate = useNavigate();

  return (
    <Layout
      title="RabbitMail"
      subtitle="Sistema de envio de e-mails em lote com RabbitMQ"
    >
      <div className="action-grid">
        <Button onClick={() => navigate("/cadastrar-email")}>
          Cadastrar um novo e-mail
        </Button>
        <Button onClick={() => navigate("/listar-emails")} variant="secondary">
          Listar e-mails cadastrados
        </Button>
        <Button onClick={() => navigate("/enviar-mensagem")} variant="success">
          Enviar uma mensagem para os e-mails cadastrados
        </Button>
      </div>
    </Layout>
  );
}

export default Home;
