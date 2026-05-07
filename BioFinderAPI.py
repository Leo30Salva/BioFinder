from typing import Optional, List
from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel, EmailStr
from sqlalchemy import create_engine, Column, Integer, String, Date, DateTime, ForeignKey, Boolean
from sqlalchemy.orm import declarative_base, sessionmaker, Session
from datetime import date, datetime
from fastapi.middleware.cors import CORSMiddleware

# ========================================================================
# 1) CONEXIÓN A MariaDB (Base de datos: animal)
# ========================================================================

DATABASE_URL = "mysql+pymysql://root:root@localhost:3306/animal"

engine = create_engine(DATABASE_URL, pool_pre_ping=True)
SessionLocal = sessionmaker(bind=engine)
Base = declarative_base()

# ========================================================================
# 2) MAPEO ORM (Modelos de Base de Datos)
# ========================================================================

class Usuario(Base):
    __tablename__ = "usuario"
    IdUser = Column(Integer, primary_key=True, autoincrement=True)
    NombreUsuario = Column(String(20), nullable=False)
    FechaCreacion = Column(DateTime, default=datetime.now)
    FechaNacimiento = Column(Date, nullable=False)
    Ciudad = Column(Integer, nullable=False)
    Email = Column(String(30), nullable=False, unique=True)
    Password = Column(String(30), nullable=False)

class Animal(Base):
    __tablename__ = "animal"
    IdAnimal = Column(Integer, primary_key=True, autoincrement=True)
    NombreAnimal = Column(String(30), nullable=False)
    NombreCientifico = Column(String(50), nullable=False)
    EsperanzaVida = Column(String(25), nullable=False)
    FechaCreacion = Column(DateTime, default=datetime.now)
    Ubicacion = Column(String(40), nullable=False)
    Reproduccion = Column(String(40), nullable=False)
    Descripcion = Column(String(500), nullable=False)
    Extinto = Column(Boolean, nullable=False)
    TipoAlimentacion = Column(String(30), nullable=False)
    Especie = Column(String(30), nullable=False)
    Actividad = Column(String(15), nullable=False)

# ========================================================================
# 3) ESQUEMAS PYDANTIC (Para validación de datos JSON)
# ========================================================================

# Esquema para el registro del usuario
class UserRegister(BaseModel):
    NombreUsuario: str
    FechaNacimiento: date
    Ciudad: str
    Email: EmailStr
    Password: str

# Respuesta para el usuario
class UserResponse(BaseModel):
    IdUser: int
    NombreUsuario: str
    Email: str
    
    class Config:
        from_attributes = True

# Esquema para iniciar sesion
class UserLogin(BaseModel):
    Email: EmailStr
    Password: str

# ========================================================================
# 4) CONFIGURACIÓN DE FASTAPI Y ENDPOINTS
# ========================================================================

api = FastAPI(title="BioFinder API", version="1.1.0")

# Permite conexiones desde el Frontend
api.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Dependencia para obtener la sesión de DB
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- ENDPOINT: REGISTRO DE USUARIO ---
@api.post("/registro", response_model=UserResponse)
def registrar_usuario(user_data: UserRegister, db: Session = Depends(get_db)):

    # 1. Verifica si el email ya existe para no volver a crear otra cuenta con el mismo email
    email_exists = db.query(Usuario).filter(Usuario.Email == user_data.Email).first()

    if email_exists:
        raise HTTPException(status_code=400, detail="El email ya está registrado.")
    
    # 2. Crear el nuevo usuario
    nuevo_usuario = Usuario(
        NombreUsuario=user_data.NombreUsuario,
        FechaCreacion=datetime.now(),
        FechaNacimiento=user_data.FechaNacimiento,
        Ciudad=user_data.Ciudad,
        Email=user_data.Email,
        Password=user_data.Password
    )
    
    try:
        db.add(nuevo_usuario)
        db.commit()
        db.refresh(nuevo_usuario)
        return nuevo_usuario
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error al guardar en DB: {str(e)}")
    
# --- ENDPOINT: LOGIN ---
@api.post("/login")
def login(user_credentials: UserLogin, db: Session = Depends(get_db)):
    # Busco al usuario por email
    usuario = db.query(Usuario).filter(Usuario.Email == user_credentials.Email).first()
    
    # Verifico si existe y si la contraseña es correcta
    if not usuario or usuario.Password != user_credentials.Password:
        raise HTTPException(status_code=401, detail="Email o contraseña incorrectos")
    
    # Si todo está bien informo
    return {"mensaje": "Login exitoso", "nombre": usuario.NombreUsuario, "id": usuario.IdUser}

# endpoint para obtener la información del perfil y mostrarla
@api.get("/usuario/{user_id}")
def obtener_perfil(user_id: int, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.IdUser == user_id).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    return {
        "NombreUsuario": usuario.NombreUsuario,
        "Email": usuario.Email,
        "FechaNacimiento": usuario.FechaNacimiento,
        "Ciudad": usuario.Ciudad 
    }

# endpoint que modifica la informacion del usuario 
@api.put("/usuario/actualizar/{user_id}")
def actualizar_usuario(user_id: int, user_data: UserRegister, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.IdUser == user_id).first()
    
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    # Actualiza los campos
    usuario.NombreUsuario = user_data.NombreUsuario
    usuario.FechaNacimiento = user_data.FechaNacimiento
    usuario.Ciudad = user_data.Ciudad
    usuario.Email = user_data.Email
    usuario.Password = user_data.Password
    
    try:
        db.commit()
        return {"mensaje": "Información actualizada correctamente"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error al actualizar: {str(e)}")
    
# este endpoint lista por la especie seleccionada en la pagina de preferencias
@api.get("/animales/filtrar")
def filtrar_animales(especie: str, db: Session = Depends(get_db)):
    # Buscamos animales donde la columna 'Especie' coincida con lo que manda el usuario
    # Usamos .lower() para que no haya problemas con mayúsculas/minúsculas
    animales = db.query(Animal).filter(Animal.Especie.ilike(f"%{especie}%")).all()
    
    if not animales:
        # Si no hay de esa especie, podrías devolver una lista vacía o un error
        return []
    
    return animales
    

# --- ENDPOINT: OBTENER TODOS LOS ANIMALES ---
@api.get("/animales")
def listar_animales(db: Session = Depends(get_db)):
    return db.query(Animal).all()