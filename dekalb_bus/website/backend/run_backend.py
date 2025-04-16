if __name__ == "__main__":
    import uvicorn
    uvicorn.run("backend:app", host="localhost", port=3000, reload=False)
