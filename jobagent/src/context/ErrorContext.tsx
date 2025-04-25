import React, { createContext, useState, useContext, ReactNode } from 'react';

interface ErrorContextType {
    error: boolean;
    setError: (error: boolean) => void;
}

const ErrorContext = createContext<ErrorContextType | undefined>(undefined);

interface ErrorProviderProps {
    children: ReactNode;
}

export const ErrorProvider: React.FC<ErrorProviderProps> = ({ children }) => {
    const [error, setError] = useState<boolean>(false);

    const value = {
        error,
        setError,
    };

    return <ErrorContext.Provider value={value}>{children}</ErrorContext.Provider>;
};

export const useError = (): ErrorContextType => {
    const context = useContext(ErrorContext);

    if (context === undefined) {
        throw new Error('useError must be used inside a ErrorProvider');
    }

    return context;
};
