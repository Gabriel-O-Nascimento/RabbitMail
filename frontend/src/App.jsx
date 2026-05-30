import { Route, Routes } from "react-router-dom";
import Home from "./pages/Home.jsx";
import RegisterEmail from "./pages/RegisterEmail.jsx";
import EmailList from "./pages/EmailList.jsx";
import SendMessage from "./pages/SendMessage.jsx";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/cadastrar-email" element={<RegisterEmail />} />
      <Route path="/listar-emails" element={<EmailList />} />
      <Route path="/enviar-mensagem" element={<SendMessage />} />
    </Routes>
  );
}

export default App;
