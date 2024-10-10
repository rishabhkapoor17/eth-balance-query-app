import { createTheme } from '@mui/material/styles';
import { blue, green } from '@mui/material/colors';

const theme = createTheme({
  palette: {
    primary: {
      main: blue[500], // A blue shade for primary elements
    },
    secondary: {
      main: green[500], // A green shade for secondary elements
    },
  },
  components: {
    MuiTextField: {
      styleOverrides: {
        root: {
          marginBottom: '1rem',
        },
      },
    },
    MuiTypography: {
      styleOverrides: {
        h4: {
          marginBottom: '1.5rem',
        },
      },
    },
    MuiGrid2: {
      styleOverrides: {
        root: {
          padding: '1rem',
        },
      },
    },
  },
});

export default theme;
