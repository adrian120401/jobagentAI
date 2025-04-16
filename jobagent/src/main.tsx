import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import { ThemeProvider } from './components/ThemeProvider';
import { JobProvider } from './context/JobContext.tsx';
import { UserProvider } from './context/UserContext.tsx';

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ThemeProvider defaultTheme="dark">
            <UserProvider>
                <JobProvider>
                    <App />
                </JobProvider>
            </UserProvider>
        </ThemeProvider>
    </StrictMode>
);
