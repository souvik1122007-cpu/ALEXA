
import os
import secrets

import httpx
from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel


app = FastAPI(title="NOVA Backend")


ACCESS_TOKEN = os.getenv("NOVA_ACCESS_TOKEN", "")
AI_API_KEY = os.getenv("AI_API_KEY", "")
AI_BASE_URL = os.getenv(
    "AI_BASE_URL",
    "https://api.openai.com/v1"
).rstrip("/")
AI_MODEL = os.getenv("AI_MODEL", "gpt-4o-mini")


class Message(BaseModel):
    role: str
    content: str


class ChatRequest(BaseModel):
    messages: list[Message]


class ChatResponse(BaseModel):
    reply: str


@app.get("/health")
async def health():
    return {"status": "ok"}


@app.post("/chat", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    authorization: str = Header(default="")
):
    if not ACCESS_TOKEN:
        raise HTTPException(
            status_code=503,
            detail="Backend access token is not configured"
        )

    expected = f"Bearer {ACCESS_TOKEN}"

    if not secrets.compare_digest(authorization, expected):
        raise HTTPException(
            status_code=401,
            detail="Invalid access token"
        )

    if not AI_API_KEY:
        raise HTTPException(
            status_code=503,
            detail="AI API key is not configured"
        )

    messages = [
        {
            "role": "system",
            "content": (
                "You are NOVA, a helpful personal AI assistant. "
                "Be clear, useful, and respectful."
            )
        }
    ]

    for message in request.messages:
        if message.role not in ("user", "assistant"):
            raise HTTPException(
                status_code=400,
                detail="Invalid message role"
            )

        messages.append({
            "role": message.role,
            "content": message.content
        })

    payload = {
        "model": AI_MODEL,
        "messages": messages
    }

    headers = {
        "Authorization": f"Bearer {AI_API_KEY}",
        "Content-Type": "application/json"
    }

    try:
        async with httpx.AsyncClient(timeout=90.0) as client:
            response = await client.post(
                f"{AI_BASE_URL}/chat/completions",
                json=payload,
                headers=headers
            )

        response.raise_for_status()
        data = response.json()

        reply = data["choices"][0]["message"]["content"]

        if not isinstance(reply, str) or not reply.strip():
            raise ValueError("Empty AI response")

        return ChatResponse(reply=reply.strip())

    except httpx.HTTPStatusError as exc:
        raise HTTPException(
            status_code=502,
            detail=f"AI provider returned HTTP {exc.response.status_code}"
        )
    except (httpx.RequestError, KeyError, IndexError, ValueError):
        raise HTTPException(
            status_code=502,
            detail="Could not get a valid response from AI provider"
        )
