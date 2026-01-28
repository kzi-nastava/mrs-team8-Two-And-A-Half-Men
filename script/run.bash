# Python executable
PYTHON=python3

# Your script
SCRIPT=main.py

pids=()


$PYTHON $SCRIPT "Bulevar Despota Stefana 7a Novi Sad" "Fakultet tehnickih nauka Novi Sad" "driver@test.com" "password" & 
pids+=($!)

echo "➡ Press ENTER to stop all workers..."
read -r  # waits for ENTER


for pid in "${pids[@]}"; do
    echo "⏹ Stopping worker with PID $pid"
    kill "$pid"
done

echo "✅ All workers stopped."