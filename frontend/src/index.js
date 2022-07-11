import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import CreateSessionComponent from "./components/CreateSessionComponent";
import VotingComponent from "./components/VotingComponent";
import WebSocketClient from "./components/WebSocketClient";

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
        <React.StrictMode>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<CreateSessionComponent/>}/>
              <Route path="/session/" element={<VotingComponent/>}/>
            </Routes>
          </BrowserRouter>
          <WebSocketClient/>
        </React.StrictMode>
);
