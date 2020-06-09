import {useEffect} from 'react'

const useTransform = (value, ref) => {
    useEffect(() => {
        if(value!= 0) {
            const {x, y} = value
            ref.current.style.transform = `translate3d(${x}px, ${y}px,0)`
        }
    }, [value]) 
}

export default useTransform

