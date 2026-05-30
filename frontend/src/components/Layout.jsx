import { Link } from "react-router-dom";

function Layout({ title, subtitle, children }) {
  return (
    <main className="page">
      <section className="panel">
        <header className="page-header">
          <Link to="/" className="brand-link">RabbitMail</Link>
          <h1>{title}</h1>
          {subtitle && <p>{subtitle}</p>}
        </header>

        {children}
      </section>
    </main>
  );
}

export default Layout;
