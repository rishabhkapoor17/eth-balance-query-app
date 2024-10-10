import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { TextField, Grid2 as Grid, Typography, Button, Snackbar, Alert } from '@mui/material';
import { format, subHours } from 'date-fns';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFnsV3';

import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    ChartOptions
  } from 'chart.js';
import { Line } from 'react-chartjs-2';

ChartJS.register(
CategoryScale,
LinearScale,
PointElement,
LineElement,
Title,
Tooltip,
Legend
);

const options: ChartOptions<'line'> = {
responsive: true,
plugins: {
    legend: {
    position: 'top' as const,
    },
    title: {
    display: true,
    text: 'ETH Balance Over Time',
    },
},
};

interface BalanceRecord {
  timestamp: string;
  balance: number;
}

const api = axios.create({
    baseURL: 'http://localhost:8080',
  });

const BalanceTracker: React.FC = () => {
  const [address, setAddress] = useState<string>('');
  const [updateInterval, setUpdateInterval] = useState<number>(60000); // 60 second default
  const [balances, setBalances] = useState<BalanceRecord[]>([]);
  const [startTime, setStartTime] = useState(subHours(new Date(), 24)); // 24 hour default
  const [endTime, setEndTime] = useState(new Date());
  const [inputAddress, setInputAddress] = useState<string>('');
  const [inputInterval, setInputInterval] = useState<number>(60000);  // 60 second default
  const [inputStartTime, setInputStartTime] = useState(subHours(new Date(), 24)); // 24 hour default
  const [inputEndTime, setInputEndTime] = useState(new Date());
  const [addressError, setAddressError] = useState<string>('');
  const [intervalError, setIntervalError] = useState<string>('');

  const [flashbarVisible, setFlashbarVisible] = useState<boolean>(false);
  const [flashbarMessage, setFlashbarMessage] = useState<string>('');
  const [flashbarSeverity, setFlashbarSeverity] = useState<'success' | 'error'>('success');

  const getCurrentAddress = async (): Promise<void> => {
    try {
      const response = await api.get<string>('/api/address/current');
      console.log(response);
    } catch (error) {
      console.error('Error getting current address:', error);
    }
  };

  const getBalances = async (): Promise<void> => {
    if (!address) return;
    try {
      const response = await api.get<BalanceRecord[]>('/api/balances', {
        params: {
          address,
          start: format(startTime, "yyyy-MM-dd'T'HH:mm:ss"),
          end: format(endTime, "yyyy-MM-dd'T'HH:mm:ss"),
        },
      });
      setBalances(response.data);
    } catch (error) {
      console.error('Error fetching balances:', error);
    }
  };

  useEffect(() => {
    getCurrentAddress();
    getBalances();
    const timer = setInterval(getBalances, updateInterval);
    return () => clearInterval(timer);
  }, [address, updateInterval, startTime, endTime]);

  const validateAddress = (address: string) => {
    if (!/^0x[a-fA-F0-9]{40}$/.test(address)) {
        setAddressError('Invalid Ethereum address. It must start with "0x" and be 42 characters long.');
        return false;
    }
    setAddressError('');
    return true;
};

const validateInterval = (interval: number) => {
    if (interval < 1000 || interval > 86400000) { // between 1 second and 1 day in milliseconds
        setIntervalError('Query interval must be between 1000 ms and 86400000 ms.');
        return false;
    }
    setIntervalError('');
    return true;
};
  

  const handleAddressChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputAddress(event.target.value);
  };
  
  const handleIntervalChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputInterval(parseInt(event.target.value, 10));
  };
  
  const handleStartTimeChange = (newStartTime: Date | null) => {
    if (newStartTime) {
        setInputStartTime(newStartTime);
    }
  }
  const handleEndTimeChange = (newEndTime: Date | null) => {
    if (newEndTime) {
        setInputEndTime(newEndTime)
    }
  }

  const handleFlashbarClose = () => {
    setFlashbarVisible(false);
  };

  const handleApplyChanges = async () => {
    if (!validateAddress(inputAddress) || !validateInterval(inputInterval)) {
        return; 
    }
    try {
      let changesMade = false;
      let successMessage = 'Changes applied successfully!';

      if (inputAddress !== address) {
        await api.post('/api/address/current', inputAddress, {
            headers: { 'Content-Type': 'text/plain' } 
        });
        setAddress(inputAddress);
        changesMade = true;
        successMessage += ` Address changed to ${inputAddress}.`;
      }
      
      if (inputInterval !== updateInterval) {
        await api.post('/api/update-interval', null, {
          params: { intervalMs: inputInterval },
        });
        setUpdateInterval(inputInterval);
        changesMade = true;
        successMessage += ` Query interval changed to ${inputInterval} ms.`;
      }
  
      if (inputStartTime !== startTime || inputEndTime !== endTime) {
        setStartTime(inputStartTime);
        setEndTime(inputEndTime);
        changesMade = true;
        successMessage += ` Start time changed to ${format(inputStartTime, "yyyy-MM-dd'T'HH:mm:ss")}, End time changed to ${format(inputEndTime, "yyyy-MM-dd'T'HH:mm:ss")}.`
      }

      if (changesMade) {
        setFlashbarMessage(successMessage); 
        setFlashbarSeverity('success');
        setFlashbarVisible(true);
      }
      
    } catch (error) {
      console.error('Error applying changes:', error);
      setFlashbarMessage('Failed to apply changes. Please check the console for error logs.'); 
      setFlashbarSeverity('error');
      setFlashbarVisible(true);
    }
  };

  const chartData = {
    labels: balances.map(b => format(new Date(b.timestamp), 'HH:mm:ss')),
    datasets: [
      {
        label: 'Balance',
        data: balances.map(b => b.balance),
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.5)',
      },
    ],
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Grid container spacing={2}>
        <Grid  size={{ xs: 12 }} >
          <Typography variant="h4">Balance Tracker</Typography>
        </Grid>
        <Grid  size={{ xs: 6 }}>
          <TextField
            fullWidth
            label="Address"
            value={inputAddress}
            onChange={handleAddressChange}
            error={!!addressError}
                       helperText={addressError}
            InputProps={{
              style: { fontSize: '0.9rem' }
            }}
          />
        </Grid>
        <Grid size={{ xs: 6 }}>
          <TextField
            fullWidth
            type="number"
            label="Query Interval (ms)"
            data-testid='interval-input'
            value={inputInterval}
            onChange={handleIntervalChange}
            error={!!intervalError}
            helperText={intervalError}
          />
        </Grid>
        <Grid size={{ xs: 6, sm: 3 }}>
          <DateTimePicker
            label="Start Time"
            value={inputStartTime}
            onChange={handleStartTimeChange}
          />
        </Grid>
        <Grid size={{ xs: 6, sm: 3 }}>
          <DateTimePicker
            label="End Time"
            value={inputEndTime}
            onChange={handleEndTimeChange}
          />
        </Grid>
        <Grid size={{ xs: 8, sm: 4 }}>
          <Button 
            fullWidth 
            variant="contained" 
            color="primary" 
            onClick={handleApplyChanges}
          >
            Apply Changes
          </Button>
        </Grid>
        <Grid size={{ xs: 12}}>
            <Line options={options} data={chartData} />
        </Grid>
      </Grid>
      <Snackbar open={flashbarVisible} autoHideDuration={6000} onClose={handleFlashbarClose}>
              <Alert onClose={handleFlashbarClose} severity={flashbarSeverity} sx={{ width: '100%' }}>
                  {flashbarMessage}
              </Alert>
      </Snackbar>
    </LocalizationProvider>
  );
};

export default BalanceTracker;
