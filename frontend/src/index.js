import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.scss';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import CreateSessionComponent from "./components/CreateSessionComponent";
import VotingSessionComponent from "./components/vote/VotingSessionComponent";
import WebSocketClient from "./components/WebSocketClient";

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
        <>
          <WebSocketClient/>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<CreateSessionComponent/>}/>
              <Route path="/session/*" element={<VotingSessionComponent/>}/>
            </Routes>
          </BrowserRouter>
        </>
);
