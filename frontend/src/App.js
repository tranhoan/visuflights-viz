import React from 'react';
import './App.css';
import MainPage from './Components/MainPage';
import Header from './Components/Header';

function App() {
  return (
    <React.Fragment>
      <Header/>
      <div className="content">
        <MainPage/>
      </div>
    </React.Fragment>
  );
}

export default App;
