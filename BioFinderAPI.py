from typing import Optional, List
from fastapi import FastAPI, HTTPException, Depends, Query
from pydantic import BaseModel, EmailStr
from sqlalchemy import create_engine, Column, Integer, String, Date, DateTime, ForeignKey, Boolean
from sqlalchemy.orm import declarative_base, sessionmaker, Session
from datetime import date, datetime
from fastapi.middleware.cors import CORSMiddleware
from passlib.context import CryptContext # para cifrar las contraseñas

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
    Ubicacion = Column(String(1000), nullable=False)
    Reproduccion = Column(String(40), nullable=False)
    Descripcion = Column(String(500), nullable=False)
    ImagenURL = Column(String(500), nullable=False)
    Extinto = Column(Boolean, nullable=False)
    TipoAlimentacion = Column(String(30), nullable=False)
    Especie = Column(String(30), nullable=False)
    Actividad = Column(String(15), nullable=False)

class Historial(Base):
    __tablename__ = "historial"
    IdHistorial = Column(Integer, primary_key=True, autoincrement=True)
    IdUser = Column(Integer, ForeignKey("usuario.IdUser", ondelete="CASCADE"), nullable=False)
    IdAnimal = Column(Integer, ForeignKey("animal.IdAnimal", ondelete="CASCADE"), nullable=False)
    CantidadConsultas = Column(Integer, default=1)
    UltimaConsulta = Column(DateTime, default=datetime.now, onupdate=datetime.now)

class Favorito(Base):
    __tablename__ = "favoritos"
    IdFavorito = Column(Integer, primary_key=True, autoincrement=True)
    IdUser = Column(Integer, ForeignKey("usuario.IdUser", ondelete="CASCADE"), nullable=False)
    IdAnimal = Column(Integer, ForeignKey("animal.IdAnimal", ondelete="CASCADE"), nullable=False)

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
    allow_credentials=True,
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

# Variable del cifrado bcrypt
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# endpoint para el registro de usuario
@api.post("/registro", response_model=UserResponse)
def registrar_usuario(user_data: UserRegister, db: Session = Depends(get_db)):

    # Verifico si el email ya existe
    email_exists = db.query(Usuario).filter(Usuario.Email == user_data.Email).first()

    if email_exists:
        raise HTTPException(status_code=400, detail="El email ya está registrado.")
    
    # Hasheo la contraseña que el usuario introdujo
    hashed_password = pwd_context.hash(user_data.Password)
    
    # Creo el nuevo usuario
    nuevo_usuario = Usuario(
        NombreUsuario=user_data.NombreUsuario,
        FechaCreacion=datetime.now(),
        FechaNacimiento=user_data.FechaNacimiento,
        Ciudad=user_data.Ciudad,
        Email=user_data.Email,
        Password=hashed_password
    )
    
    try:
        db.add(nuevo_usuario)
        db.commit()
        db.refresh(nuevo_usuario)
        return nuevo_usuario
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error al guardar en DB: {str(e)}")
    
# endpoint para el login ---
@api.post("/login")
def login(user_credentials: UserLogin, db: Session = Depends(get_db)):
    # Busco al usuario por email
    usuario = db.query(Usuario).filter(Usuario.Email == user_credentials.Email).first()
    
    # Verifico si existe el usuario
    if not usuario:
        raise HTTPException(status_code=401, detail="Email o contraseña incorrectos")
    
    # Compruebo con verify que la contraseña hasheada en la base de datos sea la mismo que la introducida en el login
    # de manera que nunca sera descifrado el hash de la base de datos
    if not pwd_context.verify(user_credentials.Password, usuario.Password):
        raise HTTPException(status_code=401, detail="Email o contraseña incorrectos")
    
    # Si todo está bien informo
    return {"mensaje": "Login exitoso", 
            "nombre": usuario.NombreUsuario, 
            "id": usuario.IdUser}

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
    
    # Actualizo los campos
    usuario.NombreUsuario = user_data.NombreUsuario
    usuario.FechaNacimiento = user_data.FechaNacimiento
    usuario.Ciudad = user_data.Ciudad
    usuario.Email = user_data.Email

    # Si el usuario cambia la contraseña la guardo hasheada
    if user_data.Password:
        usuario.Password = pwd_context.hash(user_data.Password)

    try:
        db.commit()
        return {"mensaje": "Información actualizada correctamente"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error al actualizar: {str(e)}")

# Endpoint para mostrar los animales seleccionados en las preferencias de animales
@api.get("/animales/filtrar")
def filtrar_animales(especie: str, db: Session = Depends(get_db)):
    # pongo todo en minusculas por prevenir errores
    busqueda = especie.lower()
    
    # A los reptiles y peces no se les quita el plural solo quitando la s, por lo que añado unas comprobaciones
    if busqueda == "reptiles": 
        busqueda = "reptil"
    elif busqueda == "peces": 
        busqueda = "pez"
    else:
        # Para el resto les quito la s
        busqueda = busqueda.rstrip('s')
    
    #Realizo la consulta de busqueda, ilike sirve para buscar todo aquello que contenga la palabra
    animales = db.query(Animal).filter(Animal.Especie.ilike(f"%{busqueda}%")).all()
    return animales

from sqlalchemy import or_ # Importa esto arriba con los demás de sqlalchemy

# Endpoint para el filtrado de animales
@api.get("/animales/filtrar_avanzado")
def filtrar_avanzado(
    nombre: Optional[str] = Query(None),
    especies: Optional[str] = Query(None),
    reproduccion: Optional[str] = Query(None),
    alimentacion: Optional[str] = Query(None),
    extinto: bool = False,
    db: Session = Depends(get_db)
):
    query = db.query(Animal)

    # FILTRO POR NOMBRE
    if nombre:
        query = query.filter(Animal.NombreAnimal.ilike(f"%{nombre}%"))

    #  FILTRO DE ESPECIES 
    if especies:
        lista_especies = especies.split(",")
        condiciones = []
        for esp in lista_especies:
            nombre_limpio = esp.rstrip('s')
            if nombre_limpio == "Pece": nombre_limpio = "Pez"
            condiciones.append(Animal.Especie.ilike(f"%{nombre_limpio}%"))
        query = query.filter(or_(*condiciones))
    
    # FILTRO DE REPRODUCCIÓN
    if reproduccion:
        lista_repro = reproduccion.split(",")
        condiciones_repro = []
        for rep in lista_repro:
            nombre_repro = rep.rstrip('s') # "Ovíparos" -> "Ovíparo"
            condiciones_repro.append(Animal.Reproduccion.ilike(f"%{nombre_repro}%"))
        query = query.filter(or_(*condiciones_repro))
        
    #  FILTRO DE ALIMENTACIÓN 
    if alimentacion:
        lista_alim = alimentacion.split(",")
        condiciones_alim = []
        for alim in lista_alim:
            condiciones_alim.append(Animal.TipoAlimentacion.ilike(f"%{alim}%"))
        query = query.filter(or_(*condiciones_alim))

    # FILTRO DE EXTINCIÓN 
    if not extinto:
        query = query.filter(Animal.Extinto == False)

    return query.all()

# Endpoint para la busueda de animales
@api.get("/animales/{id_animal}")
def obtener_animal_individual(id_animal: int, db: Session = Depends(get_db)):
    # Busco el animal que el usuario esta buscando
    animal = db.query(Animal).filter(Animal.IdAnimal == id_animal).first()
    
    if not animal:
        raise HTTPException(status_code=404, detail="Animal no encontrado")
    
    return animal

# endpoint para el historial

@api.post("/historial/registrar")
def registrar_en_historial(user_id: int, animal_id: int, db: Session = Depends(get_db)):
    # Busco si ya existe la entrada en el historial para ese usuario y animal
    entrada = db.query(Historial).filter(
        Historial.IdUser == user_id, 
        Historial.IdAnimal == animal_id
    ).first()

    if entrada:
        # Si ya existe, sumo 1 a la consulta y la fecha se actualizará sola
        entrada.CantidadConsultas += 1
    else:
        # Si no existe en el historial lo añado
        nueva_entrada = Historial(IdUser=user_id, IdAnimal=animal_id, CantidadConsultas=1)
        db.add(nueva_entrada)
    
    try:
        db.commit()
        return {"mensaje": "Historial actualizado"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=str(e))

# Get para mostrar en la pantalla del historial
@api.get("/historial/{user_id}")
def obtener_historial_usuario(user_id: int, db: Session = Depends(get_db)):
    # Añado .limit(30) al final de la consulta para que el historial tenga un límite
    resultados = db.query(Animal).join(Historial).filter(
        Historial.IdUser == user_id
    ).order_by(Historial.UltimaConsulta.desc()).limit(30).all()
    
    return resultados

# endpoint para eliminar un usuario
@api.delete("/usuario/eliminar/{user_id}")
def eliminar_usuario(user_id: int, db: Session = Depends(get_db)):
    # Lo busco
    usuario = db.query(Usuario).filter(Usuario.IdUser == user_id).first()
    
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    try:
        # Como el historial y favoritos tienen el DELETE CASCADE al borrar el usuario su historial y favoritos también serán borrados
        db.delete(usuario)
        db.commit()
        return {"mensaje": "Cuenta eliminada con éxito"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error al eliminar: {str(e)}")
    
# endpoint para agregar favoritos
@api.post("/favoritos/agregar")
def agregar_favorito(user_id: int, animal_id: int, db: Session = Depends(get_db)):
    # Verifico si el usuario existe
    usuario = db.query(Usuario).filter(Usuario.IdUser == user_id).first()
    if not usuario:
        raise HTTPException(status_code=400, detail=f"El usuario con ID {user_id} no existe")

    # Verifico si el animal existe
    animal = db.query(Animal).filter(Animal.IdAnimal == animal_id).first()
    if not animal:
        raise HTTPException(status_code=400, detail=f"El animal con ID {animal_id} no existe")

    # Verificar si ya está en favoritos
    existe = db.query(Favorito).filter(Favorito.IdUser == user_id, Favorito.IdAnimal == animal_id).first()
    if existe:
        return {"mensaje": "Ya está en favoritos"}
    
    # Si aún no está en favoritos entonces se añadirá un nuevo registro en la tabla de favoritos del usuario
    nuevo_favorito = Favorito(IdUser=user_id, IdAnimal=animal_id)
    try:
        db.add(nuevo_favorito)
        db.commit()
        return {"mensaje": "Añadido a favoritos"}
    except Exception as e:
        db.rollback()
        print(f"Error real: {e}") # Esto saldrá en tu terminal negra
        raise HTTPException(status_code=500, detail=str(e))
    
# endpoint para ver los favoritos
@api.get("/favoritos/usuario/{user_id}")
def obtener_favoritos(user_id: int, db: Session = Depends(get_db)):
    # Esta consulta une la tabla favoritos con animal para mostrar los datos del animal
    return db.query(Animal).join(Favorito).filter(Favorito.IdUser == user_id).all()

# endpoint para eliminar de favoritos
@api.delete("/favoritos/eliminar")
def eliminar_favorito(user_id: int, animal_id: int, db: Session = Depends(get_db)):
    # busco el registro
    favorito = db.query(Favorito).filter(
        Favorito.IdUser == user_id, 
        Favorito.IdAnimal == animal_id
    ).first()

    if not favorito:
        raise HTTPException(status_code=404, detail="El favorito no existe")

    # borro el registro
    try:
        db.delete(favorito)
        db.commit()
        return {"mensaje": "Eliminado de favoritos correctamente"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=str(e))

# endpoint para ver todos los animales
@api.get("/animales")
def listar_animales(db: Session = Depends(get_db)):
    return db.query(Animal).all()