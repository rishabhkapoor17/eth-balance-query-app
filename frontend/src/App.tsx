import React from 'react';
import BalanceTracker from './components/balance-tracker';
import { Container, CssBaseline, ThemeProvider } from '@mui/material';
import theme from './theme';

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
        <Container maxWidth="lg">
          <BalanceTracker />
        </Container>
    </ThemeProvider>
  );
}

export default App;