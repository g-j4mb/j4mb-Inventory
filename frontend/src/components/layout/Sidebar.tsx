
type SidebarProps = {
  activePage: string;
  onNavigate: (page: string) => void;
};

const navigation = [
  { label: 'Dashboard', icon: '▦' },
  { label: 'Products', icon: '□' },
  { label: 'Suppliers', icon: '♧' },
  { label: 'Purchasing', icon: '⇄' },
  { label: 'Inventory', icon: '▤' },
  { label: 'Dispatch', icon: '➜' },
  { label: 'Returns', icon: '↶' },
];

export default function Sidebar({
  activePage,
  onNavigate,
}: SidebarProps) {
  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-logo">I</div>
        <div>
          <h1>IMS</h1>
          <p>Inventory Management</p>
        </div>
      </div>

      <div className="nav-heading">WORKSPACE</div>

      <nav className="navigation">
        {navigation.map((item) => (
          <button
            key={item.label}
            className={`nav-item ${
              activePage === item.label ? 'active' : ''
            }`}
            onClick={() => onNavigate(item.label)}
          >
            <span className="nav-icon">{item.icon}</span>
            <span>{item.label}</span>
          </button>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="avatar">AD</div>
        <div>
          <strong>Administrator</strong>
          <p>System user</p>
        </div>
      </div>
    </aside>
  );
}
