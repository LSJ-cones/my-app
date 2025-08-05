import { useEffect, useState } from 'react';

function App() {
  const [msg, setMsg] = useState('');

  useEffect(() => {
    fetch('/api/hello')
      .then(res => res.text())
      .then(setMsg)
      .catch(err => setMsg("에러: " + err));
  }, []);

  return (
    <div>
      <h1>React → Spring 테스트</h1>
      <p>{msg}</p>
    </div>
  );
}

export default App;
