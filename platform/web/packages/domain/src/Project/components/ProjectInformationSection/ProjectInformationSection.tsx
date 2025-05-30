import { useFormComposable } from '@komune-io/g2'
import { Stack } from '@mui/material'
import {
    Project,
    ProjectBanner,
    ProjectDetails,
} from 'domain-components'

export interface ProjectInformationSectionProps {
    project?: Project
    isLoading: boolean
}

export const ProjectInformationSection = (props: ProjectInformationSectionProps) => {
    const { isLoading, project } = props

    const formState = useFormComposable({
        onSubmit: () => { },
        isLoading: isLoading,
        readOnly: true,
        formikConfig: {
            initialValues: {
                ...project,
                location: project?.location ? { position: { lat: project?.location?.lat, lng: project?.location?.lon } } : undefined,
            }
        }
    })

    return (<>
        <ProjectBanner formState={formState} />
        <Stack direction="row" gap={7}>
            <ProjectDetails formState={formState} />
            {/*<ProjectProtocolesLocation formState={formState} />*/}
        </Stack>
      </>
    )
}
