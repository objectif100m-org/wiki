import { Box } from '@mui/material'
import { CatalogueCard } from '../CatalogueCard'
import {Catalogue, CatalogueRef} from '../../model'

export interface CatalogueGridProps {
    items?: Catalogue[] | CatalogueRef[]
    isLoading?: boolean
}

export const CatalogueGrid = (props: CatalogueGridProps) => {
    const { items, isLoading } = props
    return (
        <Box
            sx={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(282px, 1fr))"
            }}
            gap={3}
        >
            { isLoading ?
                    Array.from({length: 4}, (_, index) => (
                        <CatalogueCard
                            key={index}
                            isLoading
                        />
                    ))
                :
                items?.map((catalogue) => (
                    <CatalogueCard key={catalogue.identifier} catalogue={catalogue} />
                ))
            }
        </Box>
    )
}
