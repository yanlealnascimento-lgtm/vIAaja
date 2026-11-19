import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import RotaProtegida from './auth/RotaProtegida.jsx'
import Comparador from './pages/Comparador.jsx'
import Entrar from './pages/Entrar.jsx'
import Cadastro from './pages/Cadastro.jsx'
import RecuperarSenha from './pages/RecuperarSenha.jsx'
import Busca from './pages/Busca.jsx'
import Assistente from './pages/Assistente.jsx'
import Itinerarios from './pages/Itinerarios.jsx'
import ItinerarioDetalhe from './pages/ItinerarioDetalhe.jsx'
import Social from './pages/Social.jsx'
import Reuniao from './pages/Reuniao.jsx'
import Destinos from './pages/Destinos.jsx'
import DestinoDetalhe from './pages/DestinoDetalhe.jsx'
import Perfil from './pages/Perfil.jsx'
import PainelParceiro from './pages/PainelParceiro.jsx'
import PainelAdmin from './pages/PainelAdmin.jsx'
import NaoEncontrada from './pages/NaoEncontrada.jsx'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Comparador />} />
        <Route path="/entrar" element={<Entrar />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/recuperar-senha" element={<RecuperarSenha />} />
        <Route path="/busca" element={<Busca />} />
        <Route path="/destinos" element={<Destinos />} />
        <Route path="/destinos/:id" element={<DestinoDetalhe />} />

        <Route path="/assistente" element={
          <RotaProtegida perfis={['VIAJANTE']}><Assistente /></RotaProtegida>
        } />
        <Route path="/itinerarios" element={
          <RotaProtegida perfis={['VIAJANTE']}><Itinerarios /></RotaProtegida>
        } />
        <Route path="/itinerarios/:id" element={
          <RotaProtegida perfis={['VIAJANTE']}><ItinerarioDetalhe /></RotaProtegida>
        } />
        <Route path="/social" element={
          <RotaProtegida perfis={['VIAJANTE']}><Social /></RotaProtegida>
        } />
        <Route path="/reuniao/:salaId" element={
          <RotaProtegida perfis={['VIAJANTE']}><Reuniao /></RotaProtegida>
        } />
        <Route path="/perfil" element={
          <RotaProtegida perfis={['VIAJANTE']}><Perfil /></RotaProtegida>
        } />
        <Route path="/parceiro" element={
          <RotaProtegida perfis={['PARCEIRO']}><PainelParceiro /></RotaProtegida>
        } />
        <Route path="/admin" element={
          <RotaProtegida perfis={['ADMINISTRADOR']}><PainelAdmin /></RotaProtegida>
        } />

        <Route path="*" element={<NaoEncontrada />} />
      </Routes>
    </Layout>
  )
}
